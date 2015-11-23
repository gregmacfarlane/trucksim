
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;

public class VolumeCalculator {

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		// TODO Auto-generated method stub
		/*
		 *  Inputs defined with @param statements above.
		 *
		 *  Also, reads the network from the scenario configuration.
		 *  I can't actually tell that the network is necessary, but
		 *  `net` is a required input to `MyVolumesAnalyzer`, so I'm
		 *  including it.
		 */
		String configFile = args[0];
		String eventsFile = args[1];
		String outputFile = args[2];
		int timePeriod = 3600*24; // Daily volumes
		int maxTime = 3600*24*7;    // seven days
		
		
		Config config = ConfigUtils.loadConfig(configFile);
		Scenario sc = ScenarioUtils.createScenario(config);
		Network net = sc.getNetwork();
		
		
		/*
		 * Here I create an instance of MyVolumesAnalyzer, which is simply
		 * VolumesAnalyzer from the org.matsim.analysis package but edited
		 * to permit an analysis period longer than 24 hours. For now I cut
		 * this off at seven days, but we can edit if necessary.
		 *
		 */
		
		EventsManager eventsManager = EventsUtils.createEventsManager();
		MyVolumesAnalyzer handler1 = new MyVolumesAnalyzer(timePeriod, maxTime, net);
		eventsManager.addHandler(handler1);
		
		MatsimEventsReader reader = new MatsimEventsReader(eventsManager);
		reader.readFile(eventsFile);

		System.out.println("Done reading events file!");
		
	
		/*
		 * Writing the output.
		 *
		 * I'm sure that someone with better Java skills would be able to
		 * make this lots and lots cleaner, but it works! Miraculously!
		 *
		 * Basically, I create a csv file with links in the rows and by-
		 * period volumes in the columns. The first chunk creates the header.
		 * The second set grabs all of the linkIds from the EventHandler,
		 * and prints the linkIds and the period volumes.
		 *
		 */
		// open filestream
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
		
		// create table header
		String[] periods = new String[maxTime/timePeriod + 2];
		for (int i = 0; i < periods.length; i++ ) {
			periods[i] = "Period_" + String.valueOf(i);
		}
		String header = Arrays.toString(periods)
					.replace("]", "")
					.replace("[", "");
		writer.println("Link, " + header);
		
		// get linkIds, and loop through links to print volumes.
		Set<Id> linkIds = handler1.getLinkIds();
		Iterator<Id> iter = linkIds.iterator();
		while(iter.hasNext()){
			Id key = iter.next();
			int[] volumes = handler1.getVolumesForLink(key);
			String outputVolumes = Arrays.toString(volumes)
					.replace("]", "")
					.replace("[", "");
			writer.println(key + ", " + outputVolumes);
		}
		
		writer.close();
	}
	
}
