package callcenter;

import callcenter.utils.Utils;

public class Call implements Leveled {
    private String id;
    private Level issueLevel; //this level will not be seen in a call center, only Responser can see this
    private Level level;  // responser will decide call level
    private int duration;

    public Call(Level issueLevel, Level level, int duration) {
        this.id = "c"+ Utils.genCallId();
        this.issueLevel = issueLevel;
        this.level = level;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public Level getIssueLevel() {
        return issueLevel;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
    public int getDuration() {
        return duration;
    }

    public static Call genByLevel(Level level) {
        return genByLevel(level, 0);
    }
    public static Call genByLevel(Level level, int duration) { //generate a call with  known level
        return new Call(level, level, duration);
    }

    public static Call genByIssueLevel(Level issueLevel) {
        return genByIssueLevel(issueLevel, 0);
    }
    public static Call genByIssueLevel(Level issueLevel, int duration) { //generate a call with unknown level
        return new Call(issueLevel, null, duration);
    }

}
