package op.algorithm;

/**
 * Enum specifying all available scheduling algorithm implementations that the user may specify at the command line
 * @author Darcy Cox
 */
public enum Algorithm {
    DFS("dfs"),
    ASTAR("astar"),
    SIMPLE("simple"),
    GREEDY("greedy");

    private String cmdRepresentation;

    Algorithm(String cmdRepresentation) {
        this.cmdRepresentation = cmdRepresentation;
    }

    public String getCmdRepresentation() {
        return this.cmdRepresentation;
    }

}
