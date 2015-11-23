/**
 *
 */

import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;

/**
 * @author Greg
 *
 */
public class Controller {

	/**
	 * @param args
	 * First argument should be the name of the xml configuration file.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String configfile = args[0];
		Config config = ConfigUtils.loadConfig(configfile);
		Controler controler = new Controler(config);
		
		controler.setCreateGraphs(false);
		controler.run();

	}

}
