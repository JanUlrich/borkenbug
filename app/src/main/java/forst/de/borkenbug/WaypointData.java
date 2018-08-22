package forst.de.borkenbug;

class WaypointData {
    private final String tree;
    private final String bug;
    private final int size;
    private final int fm;

    public WaypointData(String baumart, String schadensart, int fm, int fläche){
        this.tree = baumart;
        this.bug = schadensart;
        this.fm = fm;
        this.size = fläche;
    }

    public String toOSMText() {
        return tree + "\t" + bug + " FM²: "+ fm + " Fläche: " + size + "\tOl_icon_red_example.png\t16,16\t-8,-8";
    }
}
