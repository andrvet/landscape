package landscape;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlRootElement;


import com.sun.jersey.spi.resource.Singleton;

/*
 * Adding Road
POST hostname:8080/site/{site}/road/{anotherSite}?distance=XX

Get all road from current site
GET hostname:8080/site/{site}/roads

get distance b/w two sites (if direct connection (road) exits) 
GET hostname:8080/site/{site}/road/{anotherSite}

Could be multiple roads from one site to another site (with different distances) - so in removeRoad() we check distance
In this case add road API could be:
POST hostname:8080/site/{site}/road/{anotherSite}/distance/{distanceValue}  
but hostname:8080/site/{site}/road/{anotherSite}?distance=XX) maybe better 
 */

@Produces({"application/xml", "application/json"}) 
@Path("sites") 
@Singleton
public class SiteStructure {

}

class Landscape {

    public Map<String, Site> sites;

	@POST 	@Path("{name}") @Produces({"application/xml", "application/json"}) 
	public Site addSite(@PathParam("name") String name) {
        Site newSite = new Site(name);
        if (sites.containsKey(name)) {
            return sites.get(name);
        }
        sites.put(name, newSite);
        return newSite;
    }
	@GET 	@Path("{name}")	@Produces({"application/xml", "application/json"}) 	
	public Site getSite(@PathParam("name") String name) {
        if (sites.containsKey(name)) {
            return sites.get(name);
        }
        return null;
    }

    @XmlRootElement
    static class Site {
        private String name;
        private Set<Road> roads;

        public Site(String name) {
            this.name = name;
            roads = new HashSet<Road>();
        }

        //POST hostname:8080/site/{site}/road/{anotherSite}?distance=XX //TODO
        @POST 	@Path("{name}") @Produces({"application/xml", "application/json"}) 
        public Site addRoad(Site site, @PathParam("distance") Double distance) {
        //public Site addRoad(Site site, double distance) {
            Road road = new Road(this, site, distance);
            roads.add(road);
            site.roads.add(road);
            return this;
        }

        public Site removeRoad(Site site, double distance) {
            Road road = new Road(this, site, distance);
            roads.remove(road);
            site.roads.remove(road);
            return this;
        }

        public Site updateRoad(Site site, double oldDistance, double newDistance) {
            removeRoad(site, oldDistance);
            addRoad(site, newDistance);
            return this;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Site other = (Site) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

    }
    
    @XmlRootElement
    static class Road {
        public Site from;
        public Site to;
        public double distance;

        public Road(Site from, Site to, double distance) {
            super();
            this.from = from;
            this.to = to;
            this.distance = distance;
        }

    }
}