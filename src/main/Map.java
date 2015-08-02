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
    private int verticalRoads;
    private int horizontalRoads;
    private Position[][] intersections;
    private LightModel lightModel;
    private int width;
    private int length;

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

    public Intersection[] getIntersections(){
        Intersection[] is = new Intersection[this.getNumberIntersections()];
        for (int i=0; i<this.getNumberIntersections(); i++){
        }
        return is;
    }

    public LocationType getLocationType(int x, int y){
        return mapLocations[x][y].locationtype;
    }

    public void print(){
        for (int i=0; i<this.length; i++){
            for (int j = 0; j<this.width; j++){
                System.out.print(mapLocations[i][j].locationtype.toString().charAt(0)+" ");
            }
            System.out.println();
        }
    }
}
