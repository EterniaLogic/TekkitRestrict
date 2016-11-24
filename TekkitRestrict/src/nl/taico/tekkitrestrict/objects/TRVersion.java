package nl.taico.tekkitrestrict.objects;

import nl.taico.tekkitrestrict.config.SettingsStorage;

public class TRVersion {
	public String fullVer;
	public int major;
	public int minor;
	public boolean beta;
	public boolean dev;
	public int extra = -1;

	public TRVersion(String str){
		if (str == null) return;
		//str = str.toLowerCase();
		if (str.toLowerCase().contains("tekkitrestrict")){
			str = str.replaceAll("(?i)tekkitrestrict", "");
		}
		str = str.trim();
		fullVer = str;
		{
			final String[] temp = fullVer.split(" ")[0].split("\\.");
			major = Integer.parseInt(temp[0]);
			minor = Integer.parseInt(temp[1]);
		}

		if (!fullVer.contains(" ")){
			beta = false;
			dev = false;
			extra = -1;
		} else if (fullVer.toLowerCase().contains("beta")){
			beta = true;
			dev = false;

			final String[] temp = fullVer.split(" ");//1.18,beta,1
			if (temp.length == 2)
				extra = 1;
			else if (temp.length >= 3)
				extra = Integer.parseInt(temp[2]);
		} else if (fullVer.toLowerCase().contains("dev")){
			beta = false;
			dev = true;

			final String[] temp = fullVer.split(" ");//1.18,dev
			if (temp.length == 2)
				extra = 1;
			else if (temp.length >= 3)
				extra = Integer.parseInt(temp[2]);
		}
	}

	public boolean isNewer(final TRVersion ver2){
		if (ver2 == null) return false;
		if (this.major > ver2.major) return false;
		if (this.minor > ver2.minor) return false;

		if (beta){													//1.18 beta
			if (!ver2.beta && !ver2.dev) return true;				//not beta, not dev, at least 1.18 release, so newer
			if (ver2.beta){
				if (ver2.minor > this.minor) return true;			//beta 1.19 newer than beta 1.18

				if (ver2.minor == this.minor){						//both 1.18 beta
					if (ver2.extra > this.extra) return true;		//1.18 beta 2 newer than 1.18 beta 1
					return false;									//1.18 beta x not newer than 1.18 beta x
				}
			}

			return false;											//dont update to dev versions from beta versions 
		} else if (dev){
			if (!ver2.beta && !ver2.dev) return true;				//not beta, not dev, at least 1.18 release, so newer
			if (ver2.dev){
				if (ver2.minor > this.minor) return true;			//dev 1.19 newer than dev 1.18

				if (ver2.minor == this.minor){						//both 1.18 dev
					if (ver2.extra > this.extra) return true;		//1.18 dev 2 newer than 1.18 dev 1
					return false;									//1.18 dev x not newer than 1.18 dev x
				}
			}

			return false;											//dont update to beta versions from dev versions
		} else {													//1.18 release
			if (ver2.beta || ver2.dev) return false;				//dont update to devs or betas, not even newer ones.
			if (ver2.minor == this.minor) return false;				//both 1.18 release
			return true;
		}
	}

	public boolean shouldUpdate(final TRVersion ver2){
		if (ver2 == null) return false;
		if (this.major > ver2.major) return false;
		if (this.minor > ver2.minor) return false;

		if (beta){													//1.18 beta
			if (!ver2.beta && !ver2.dev) return true;				//not beta, not dev, at least 1.18 release, so newer
			if (ver2.beta){
				if (ver2.minor > this.minor) return true;			//beta 1.19 newer than beta 1.18

				if (ver2.minor == this.minor){						//both 1.18 beta
					if (ver2.extra > this.extra) return true;		//1.18 beta 2 newer than 1.18 beta 1
					return false;									//1.18 beta x not newer than 1.18 beta x
				}
			} else {												//1.18 dev
				if (SettingsStorage.generalConfig.getBoolean("UpdateToDevelopmentVersions", false)){
					if (ver2.minor > this.minor) return true;		//update to new dev from beta
					return false;									//only update to devs on new minors.
				}
				return false;										//dont update to dev versions from beta
			}

			return false;											//dont update to dev versions from beta versions 
		} else if (dev){
			if (!ver2.beta && !ver2.dev) return true;				//not beta, not dev, at least 1.18 release, so newer
			if (ver2.dev){
				if (ver2.minor > this.minor) return true;			//dev 1.19 newer than dev 1.18

				if (ver2.minor == this.minor){						//both 1.18 dev
					if (ver2.extra > this.extra) return true;		//1.18 dev 2 newer than 1.18 dev 1
					return false;									//1.18 dev x not newer than 1.18 dev x
				}
			} else {												//new == 1.18 beta
				if (SettingsStorage.generalConfig.getBoolean("UpdateToBetaVersions", false)){
					if (ver2.minor >= this.minor) return true;		//update to new beta indeed. Also update to beta on same minor, as that is newer
					return false;									//impossible?
				}
				return false;										//dont update to beta versions from dev
			}

			return false;											//dont update to beta versions from dev versions
		} else {													//1.18 release
			if (ver2.beta || ver2.dev){								//dont update to devs or betas, not even newer ones.
				if (ver2.minor == this.minor) return false;			//dont update to lower version
				if (ver2.beta && SettingsStorage.generalConfig.getBoolean("UpdateToBetaVersions", false)){
					return true;									//1.19 beta is newer than 1.18 release
				} else if (ver2.dev && SettingsStorage.generalConfig.getBoolean("UpdateToDevelopmentVersions", false)){
					return true;									//1.19 dev is newer than 1.18 release
				}
				return false;
			} else {
				if (ver2.minor == this.minor) return false;			//1.18 release = 1.18 release
				return true;										//1.19 release > 1.18 release
			}
		}
	}

	public String toMetricsVersion(){
		String base = "" + major + "." + minor;
		if (beta) base += " Beta " + extra;
		else if (dev) base += " Dev " + extra;
		else base += " Release";
		return base;
	}
}
