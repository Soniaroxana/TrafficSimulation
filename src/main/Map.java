import java.util.ArrayList;

/**
 * Created by soniamarginean on 7/30/15.
 */
// Class that models the physical map
public class Map {
    // Each map is a grid of map locations (modeled by positions)
    // Each map has a # of horizontal and vertical roads, with each road having 2 lanes running in opposite directions
    public Position[][] mapLocations;
    public int verticalRoads;
    public int horizontalRoads;
    private ArrayList<LightModel> lightModels;
    public int width;
    public int length;
    public int intIndex = 0;
    public ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();

    //create a m by n positions map
    // with a passed in number of vertical and horizontal roads
    // and a set of light models corresponding to the intersections in the model
    //each intersection has 4 positions
    //each road is 2 positions wide, one for each lane
    public Map(int m, int n, int verticalRoads, int horizontalRoads, ArrayList<LightModel> lightModels){
        this.length = m;
        this.width = n;
        this.lightModels = lightModels;

        // we want to be able to have cars on the road, not just intersection, so we restrict the number of possible
        // intersections to a quarter of the map
        this.verticalRoads = verticalRoads;
        if (verticalRoads > n/4){
            this.verticalRoads = n/4;
        }

        this.horizontalRoads = horizontalRoads;
        if (horizontalRoads > n/4){
            this.horizontalRoads = n/4;
        }

        // start off with all positions as scenery
        mapLocations = new Position[m][n];
        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++){
                mapLocations[i][j] = new Position(i,j, LocationType.SCENERY);
            }
        }

        // as we go through the horizontal and vertical roads, place them evenly on the map, create lanes for the
        // corresponding opposite directions and change the location type and directions accordingly
        for (int i=0; i<horizontalRoads; i++){
            for (int j=0; j<n; j++){
                mapLocations[(i+1)*(m/(horizontalRoads+1))][j].locationtype = LocationType.LANE;
                mapLocations[(i+1)*(m/(horizontalRoads+1))][j].directions.add(Direction.WEST);
                mapLocations[(i+1)*(m/(horizontalRoads+1))+1][j].locationtype = LocationType.LANE;
                mapLocations[(i+1)*(m/(horizontalRoads+1))+1][j].directions.add(Direction.EAST);
            }
        }

        for (int i=0; i<verticalRoads; i++){
            for (int j=0; j<m; j++){
                mapLocations[j][(i+1)*(n/(verticalRoads+1))].locationtype = LocationType.LANE;
                mapLocations[j][(i+1)*(n/(verticalRoads+1))].directions.add(Direction.SOUTH);
                mapLocations[j][(i+1)*(n/(verticalRoads+1))+1].locationtype = LocationType.LANE;
                mapLocations[j][(i+1)*(n/(verticalRoads+1))+1].directions.add(Direction.NORTH);
            }
        }

        // finally find out where the intersections are (hint - they are the only locations with 2 directions)
        // also create the intersection objects based on the light models passed in
        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++){
                if (mapLocations[i][j].directions.size() == 2){
                    mapLocations[i][j].locationtype = LocationType.INTERSECTION;
                }
            }
        }

        intersectionList = computeAllIntersections(lightModels);

    }

    // Get the direction of a position
    public ArrayList<Direction> getPositionDirection(int x, int y){
        return mapLocations[x][y].directions;
    }

    // Get the number of intersections on a map
    public int getNumberIntersections(){
        return horizontalRoads*verticalRoads;
    }

    // Method which computes the 4 positions an intersection has and creates the list of intersections with their corresponding
    // light model
    public ArrayList<Intersection> computeAllIntersections(ArrayList<LightModel> lightModel){
        int index = 0;
        ArrayList<Intersection> inter = new ArrayList<Intersection>();
        for (int i=0; i<horizontalRoads; i++){
            for (int j=0; j< verticalRoads; j++){
                ArrayList<Position> positions = new ArrayList<Position>();
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))][(j+1)*(width/(verticalRoads+1))]);
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))][(j+1)*(width/(verticalRoads+1))+1]);
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))+1][(j+1)*(width/(verticalRoads+1))]);
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))+1][(j+1)*(width/(verticalRoads+1))+1]);
                inter.add(new Intersection(lightModel.get(index), index, positions));
                index++;
            }
        }
        return inter;
    }

    // returns the map's intersection list
    public ArrayList<Intersection> getAllIntersections(){
        return intersectionList;
    }

    // Gets the intersection list in a certain direction for a certain lightmodel
    public Intersection[] getIntersections(int index, Direction dir, LightModel lightModel){
        ArrayList<Intersection> is = new ArrayList<Intersection>();
        switch (dir){
            case NORTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][(index+1)*(width/(verticalRoads+1))+1].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[i][(index+1)*(width/(verticalRoads+1))+1]);
                        positions.add(mapLocations[i][(index+1)*(width/(verticalRoads+1))]);
                        positions.add(mapLocations[i+1][(index+1)*(width/(verticalRoads+1))+1]);
                        positions.add(mapLocations[i+1][(index+1)*(width/(verticalRoads+1))]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
            case SOUTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][(index+1)*(width/(verticalRoads+1))].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[i][(index+1)*(width/(verticalRoads+1))+1]);
                        positions.add(mapLocations[i][(index+1)*(width/(verticalRoads+1))]);
                        positions.add(mapLocations[i+1][(index+1)*(width/(verticalRoads+1))+1]);
                        positions.add(mapLocations[i+1][(index+1)*(width/(verticalRoads+1))]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
            case EAST:
                for (int i=0; i<width; i++){
                    if (mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))][i]);
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i]);
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))][i+1]);
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i+1]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
            case WEST:
                for (int i=0; i<width; i++){
                    if (mapLocations[(index+1)*(length/(horizontalRoads+1))][i].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))][i]);
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i]);
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))][i+1]);
                        positions.add(mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i+1]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
        }
        Intersection[] a = new Intersection[is.size()];
        return is.toArray(a);
    }

    // Gets the intersections list in a direction dir for a road index
    // this is how a car on a certain lane going a certain direction can get the intersections it needs to cross
    public Intersection[] getMyIntersections(int index, Direction dir){
        ArrayList<Intersection> is = new ArrayList<Intersection>();
        for (Intersection i : this.getAllIntersections()){
            ArrayList<Position> pos = i.locations;
            switch (dir){
                case EAST:
                    for(Position p:pos){
                        if(p.x == (index+1)*(length/(horizontalRoads+1))+1){
                            is.add(i);
                            break;
                        }
                    }
                    break;
                case WEST:
                    for(Position p:pos){
                        if(p.x == (index+1)*(length/(horizontalRoads+1))){
                            is.add(i);
                            break;
                        }
                    }
                    break;
                case NORTH:
                    for(Position p:pos){
                        if(p.y == (index+1)*(width/(verticalRoads+1))+1){
                            is.add(i);
                            break;
                        }
                    }
                    break;
                case SOUTH:
                    for(Position p:pos){
                        if(p.y == (index+1)*(width/(verticalRoads+1))){
                            is.add(i);
                            break;
                        }
                    }
                    break;
            }
        }
        Intersection[] a = new Intersection[is.size()];
        return is.toArray(a);
    }

    // returns the list of all valid positions on the map that can be initially populated by a car
    // in a certain direction at the specified road index
    // namely these are all the positions marked as Lane on that road in direction dir
    // the car is not allowed to just sit in an intersection or in the scenery
    public ArrayList<Position> getLanePositions(int index, Direction dir){
        ArrayList<Position> pos = new ArrayList<Position>();
        switch (dir){
            case NORTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][(index+1)*(width/(verticalRoads+1))+1].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[i][(index+1)*(width/(verticalRoads+1))+1]);
                    }
                }
                break;
            case SOUTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][(index+1)*(width/(verticalRoads+1))].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[i][(index+1)*(width/(verticalRoads+1))]);
                    }
                }
                break;
            case EAST:
                for (int i=0; i<width; i++){
                    if (mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[(index+1)*(length/(horizontalRoads+1))+1][i]);
                    }
                }
                break;
            case WEST:
                for (int i=0; i<width; i++){
                    if (mapLocations[(index+1)*(length/(horizontalRoads+1))][i].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[(index+1)*(length/(horizontalRoads+1))][i]);
                    }
                }
                break;
        }
        return pos;
    }

    // gets a position's location type on this map
    public LocationType getLocationType(int x, int y){
        return mapLocations[x][y].locationtype;
    }

    // pretty prints the map using the location types of the positions
    public void print(){
        for (int i=0; i<this.length; i++){
            for (int j = 0; j<this.width; j++){
                System.out.print(" |"+mapLocations[i][j].locationtype.toString().charAt(0)+"| ");
            }
            System.out.println();
        }
    }


}
