package callcenter;

public enum Level {
    Fresher(1),
    TL(2),
    PM(3)
    ;

    int level;
    Level(int level) {
        this.level = level;
    }

    public static Level min() {
        return Fresher;
    }
    public static Level max() { return PM; }

    public int getLevel() {
        return level;
    }

    public boolean isMax() {
        return level == Level.values().length;
    }

    public Level escalate() {
        Level nextLevel = findByLevel(this.level + 1);
        return (nextLevel==null)? max() : nextLevel;
    }


    private Level findByLevel(int target) {
        for(Level lv : Level.values()) {
            if(lv.level == target) {
                return lv;
            }
        }
        return null;
    }
    
}
