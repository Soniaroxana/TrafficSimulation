import java.util.ArrayList;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class Map {
    //create a m by n positions map
    //each intersection has 4 positions
    //each road is 2 positions wide, one for each lane
    //initially one intersection and two roads
    public Position[][] mapLocations;
    public int verticalRoads;
    public int horizontalRoads;
    private Position[][] intersections;
    private LightModel lightModel;
    public int width;
    public int length;
    public int intIndex = 0;
    public ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();

    public Map(int m, int n, int verticalRoads, int horizontalRoads, LightModel lightModel){
        this.length = m;
        this.width = n;
        this.lightModel = lightModel;

        this.verticalRoads = verticalRoads;

        if (verticalRoads > n/4){
            this.verticalRoads = n/4;
        }

        this.horizontalRoads = horizontalRoads;

        if (horizontalRoads > n/4){
            this.horizontalRoads = n/4;
        }

        mapLocations = new Position[m][n];

        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++){
                mapLocations[i][j] = new Position(i,j, LocationType.SCENERY);
            }
        }

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

        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++){
                if (mapLocations[i][j].directions.size() == 2){
                    mapLocations[i][j].locationtype = LocationType.INTERSECTION;
                }
            }
        }

        intersectionList = computeAllIntersections(lightModel);

    }

    public ArrayList<Direction> getPositionDirection(int x, int y){
        return mapLocations[x][y].directions;
    }

    public int getNumberIntersections(){
        return horizontalRoads*verticalRoads;
    }

    public ArrayList<Intersection> computeAllIntersections(LightModel lightModel){
        int index = 0;
        ArrayList<Intersection> inter = new ArrayList<Intersection>();
        for (int i=0; i<horizontalRoads; i++){
            for (int j=0; j< verticalRoads; j++){
                ArrayList<Position> positions = new ArrayList<Position>();
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))][(j+1)*(width/(verticalRoads+1))]);
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))][(j+1)*(width/(verticalRoads+1))+1]);
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))+1][(j+1)*(width/(verticalRoads+1))]);
                positions.add(mapLocations[(i+1)*(length/(horizontalRoads+1))+1][(j+1)*(width/(verticalRoads+1))+1]);
                inter.add(new Intersection(lightModel, index, positions));
                index++;
            }
        }
        return inter;
    }

    public ArrayList<Intersection> getAllIntersections(LightModel lightModel){
        return intersectionList;
    }

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

    public Intersection[] getMyIntersections(int index, Direction dir, LightModel lightModel){
        ArrayList<Intersection> is = new ArrayList<Intersection>();
        for (Intersection i : this.getAllIntersections(lightModel)){
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

    public LocationType getLocationType(int x, int y){
        return mapLocations[x][y].locationtype;
    }

    public void print(){
        for (int i=0; i<this.length; i++){
            for (int j = 0; j<this.width; j++){
                System.out.print(" |"+mapLocations[i][j].locationtype.toString().charAt(0)+"| ");
            }
            System.out.println();
        }
    }


}
