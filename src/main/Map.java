import java.util.ArrayList;

/**
 * Created by soniamarginean on 7/30/15.
 */
public class Map {
    //create a 100 by 100 positions map
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
    public ArrayList<Intersection> inter = new ArrayList<Intersection>();

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
                mapLocations[i*3+1][j].locationtype = LocationType.LANE;
                mapLocations[i*3+1][j].directions.add(Direction.WEST);
                mapLocations[i*3+2][j].locationtype = LocationType.LANE;
                mapLocations[i*3+2][j].directions.add(Direction.EAST);
            }
        }

        for (int i=0; i<verticalRoads; i++){
            for (int j=0; j<m; j++){
                mapLocations[j][i*3+1].locationtype = LocationType.LANE;
                mapLocations[j][i*3+1].directions.add(Direction.SOUTH);
                mapLocations[j][i*3+2].locationtype = LocationType.LANE;
                mapLocations[j][i*3+2].directions.add(Direction.NORTH);
            }
        }

        for (int i=0; i<m; i++){
            for (int j=0; j<n; j++){
                if (mapLocations[i][j].directions.size() == 2){
                    mapLocations[i][j].locationtype = LocationType.INTERSECTION;
                }
            }
        }

    }

    public ArrayList<Direction> getPositionDirection(int x, int y){
        return mapLocations[x][y].directions;
    }

    public int getNumberIntersections(){
        return horizontalRoads*verticalRoads;
    }

    public ArrayList<Intersection> getAllIntersections(){
        return inter;
    }

    public Intersection[] getIntersections(int index, Direction dir, LightModel lightModel){
        ArrayList<Intersection> is = new ArrayList<Intersection>();
        switch (dir){
            case NORTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][index*3+2].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[i][index*3+2]);
                        positions.add(mapLocations[i][index*3+1]);
                        positions.add(mapLocations[i+1][index*3+2]);
                        positions.add(mapLocations[i+1][index*3+1]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
            case SOUTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][index*3+1].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[i][index*3+2]);
                        positions.add(mapLocations[i][index*3+1]);
                        positions.add(mapLocations[i+1][index*3+2]);
                        positions.add(mapLocations[i+1][index*3+1]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
            case EAST:
                for (int i=0; i<width; i++){
                    if (mapLocations[index*3+2][i].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[index*3+2][i]);
                        positions.add(mapLocations[index*3+1][i]);
                        positions.add(mapLocations[index*3+2][i+1]);
                        positions.add(mapLocations[index*3+1][i+1]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
            case WEST:
                for (int i=0; i<width; i++){
                    if (mapLocations[index*3+1][i].locationtype==LocationType.INTERSECTION) {
                        ArrayList<Position> positions = new ArrayList<Position>();
                        positions.add(mapLocations[index*3+2][i]);
                        positions.add(mapLocations[index*3+1][i]);
                        positions.add(mapLocations[index*3+2][i+1]);
                        positions.add(mapLocations[index*3+1][i+1]);
                        is.add(new Intersection(lightModel, intIndex, positions));
                        i++;
                        intIndex++;
                    }
                }
        }
        Intersection[] a = new Intersection[is.size()];
        inter.addAll(is);
        return is.toArray(a);
    }

    public ArrayList<Position> getLanePositions(int index, Direction dir){
        ArrayList<Position> pos = new ArrayList<Position>();
        switch (dir){
            case NORTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][index*3+2].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[i][index * 3 + 2]);
                    }
                }
                break;
            case SOUTH:
                for (int i=0; i<length; i++){
                    if (mapLocations[i][index*3+1].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[i][index * 3 + 1]);
                    }
                }
                break;
            case EAST:
                for (int i=0; i<width; i++){
                    if (mapLocations[index*3+2][i].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[index * 3 + 2][i]);
                    }
                }
                break;
            case WEST:
                for (int i=0; i<width; i++){
                    if (mapLocations[index*3+1][i].locationtype==LocationType.LANE) {
                        pos.add(mapLocations[index * 3 + 1][i]);
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
