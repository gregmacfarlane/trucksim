package trucksim;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.network.Network;

/**
 * Counts the number of vehicles leaving a link, aggregated into time bins of a specified size.
 *
 * @author mrieser
 */
public class MyVolumesAnalyzer implements LinkLeaveEventHandler, PersonDepartureEventHandler {

	private final static Logger log = Logger.getLogger(MyVolumesAnalyzer.class);
	private final int timeBinSize;
	private final int maxTime;
	private final int maxSlotIndex;
	private final Map<Id, int[]> links;
	
	// for multi-modal support
	private boolean observeModes;
	private Map<Id, String> enRouteModes;
	private Map<Id, Map<String, int[]>> linksPerMode;

	public MyVolumesAnalyzer(final int timeBinSize, final int maxTime, final Network network) {
		this(timeBinSize, maxTime, network, true);
	}
	
	public MyVolumesAnalyzer(final int timeBinSize, final int maxTime, final Network network, boolean observeModes) {
		this.timeBinSize = timeBinSize;
		this.maxTime = maxTime;
		this.maxSlotIndex = (this.maxTime/this.timeBinSize) + 1;
		this.links = new HashMap<Id, int[]>((int) (network.getLinks().size() * 1.1), 0.95f);
		
		this.observeModes = observeModes;
		if (this.observeModes) {
			this.enRouteModes = new HashMap<Id, String>();
			this.linksPerMode = new HashMap<Id, Map<String, int[]>>((int) (network.getLinks().size() * 1.1), 0.95f);
		} else {
			this.enRouteModes = null;
			this.linksPerMode = null;
		}
	}
	
	@Override
	public void handleEvent(PersonDepartureEvent event) {
		if (observeModes) {
			enRouteModes.put(event.getPersonId(), event.getLegMode());
		}
	}
	
	@Override
	public void handleEvent(final LinkLeaveEvent event) {
		int[] volumes = this.links.get(event.getLinkId());
		if (volumes == null) {
			volumes = new int[this.maxSlotIndex + 1]; // initialized to 0 by default, according to JVM specs
			this.links.put(event.getLinkId(), volumes);
		}
		int timeslot = getTimeSlotIndex(event.getTime());
		volumes[timeslot]++;
		
		if (observeModes) {
			Map<String, int[]> modeVolumes = this.linksPerMode.get(event.getLinkId());
			if (modeVolumes == null) {
				modeVolumes = new HashMap<String, int[]>();
				this.linksPerMode.put(event.getLinkId(), modeVolumes);
			}
			@SuppressWarnings("deprecation")
			String mode = enRouteModes.get(event.getPersonId());
			volumes = modeVolumes.get(mode);
			if (volumes == null) {
				volumes = new int[this.maxSlotIndex + 1]; // initialized to 0 by default, according to JVM specs
				modeVolumes.put(mode, volumes);
			}
			volumes[timeslot]++;
		}
	}

	private int getTimeSlotIndex(final double time) {
		if (time > this.maxTime) {
			return this.maxSlotIndex;
		}
		return ((int)time / this.timeBinSize);
	}

	/**
	 * @param linkId
	 * @return Array containing the number of vehicles leaving the link <code>linkId</code> per time bin,
	 * 		starting with time bin 0 from 0 seconds to (timeBinSize-1)seconds.
	 */
	public int[] getVolumesForLink(final Id linkId) {
		return this.links.get(linkId);
	}
	
	/**
	 * @param linkId
	 * @param mode
	 * @return Array containing the number of vehicles using the specified mode leaving the link 
	 *  	<code>linkId</code> per time bin, starting with time bin 0 from 0 seconds to (timeBinSize-1)seconds.
	 */
	public int[] getVolumesForLink(final Id linkId, String mode) {
		if (observeModes) {
			Map<String, int[]> modeVolumes = this.linksPerMode.get(linkId);
			if (modeVolumes != null) return modeVolumes.get(mode);
		} 
		return null;
	}
	
	/*
	 * This procedure is only working if (hour % timeBinSize == 0)
	 * 
	 * Example: 15 minutes bins
	 *  ___________________
	 * |  0 | 1  | 2  | 3  |
	 * |____|____|____|____|
	 * 0   900 1800  2700 3600
		___________________
	 * | 	  hour 0	   |
	 * |___________________|
	 * 0   				  3600
	 * 
	 * hour 0 = bins 0,1,2,3
	 * hour 1 = bins 4,5,6,7
	 * ...
	 * 
	 * getTimeSlotIndex = (int)time / this.timeBinSize => jumps at 3600.0!
	 * Thus, starting time = (hour = 0) * 3600.0
	 */
	public double[] getVolumesPerHourForLink(final Id linkId) {
		if (3600.0 % this.timeBinSize != 0) log.error("Volumes per hour and per link probably not correct!");
		
		double [] volumes = new double[72];
		for (int hour = 0; hour < 72; hour++) {
			volumes[hour] = 0.0;
		}
		
		int[] volumesForLink = this.getVolumesForLink(linkId);
		if (volumesForLink == null) return volumes;

		int slotsPerHour = (int)(3600.0 / this.timeBinSize);
		for (int hour = 0; hour < 72; hour++) {
			double time = hour * 3600.0;
			for (int i = 0; i < slotsPerHour; i++) {
				volumes[hour] += volumesForLink[this.getTimeSlotIndex(time)];
				time += this.timeBinSize;
			}
		}
		return volumes;
	}

	public double[] getVolumesPerHourForLink(final Id linkId, String mode) {
		if (observeModes) {
			if (3600.0 % this.timeBinSize != 0) log.error("Volumes per hour and per link probably not correct!");
			
			double [] volumes = new double[72];
			for (int hour = 0; hour < 72; hour++) {
				volumes[hour] = 0.0;
			}
			
			int[] volumesForLink = this.getVolumesForLink(linkId, mode);
			if (volumesForLink == null) return volumes;
	
			int slotsPerHour = (int)(3600.0 / this.timeBinSize);
			for (int hour = 0; hour < 72; hour++) {
				double time = hour * 3600.0;
				for (int i = 0; i < slotsPerHour; i++) {
					volumes[hour] += volumesForLink[this.getTimeSlotIndex(time)];
					time += this.timeBinSize;
				}
			}
			return volumes;
		}
		return null;
	}
	
	/**
	 * @return Set of Strings containing all modes for which counting-values are available.
	 */
	public Set<String> getModes() {
		Set<String> modes = new TreeSet<String>();
		
		for (Map<String, int[]> map : this.linksPerMode.values()) {
			modes.addAll(map.keySet());
		}
		
		return modes;
	}
	
	/**
	 * @return Set of Strings containing all link ids for which counting-values are available.
	 */
	public Set<Id> getLinkIds() {
		return this.links.keySet();
	}

	@Override
	public void reset(final int iteration) {
		this.links.clear();
		if (observeModes) {
			this.linksPerMode.clear();
			this.enRouteModes.clear();
		}
	}
}