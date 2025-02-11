package dev.thorny.data;

public enum Languages {
    EN("English(US)"),
    JP("Japanese"),
    CHS("Chinese"),
    KR("Korean");

    private final String name;
    
    public String getName() {
        return this.name;
    }

    Languages(final String name) {
        this.name = name;
    }
}
