package com.fges.model;

/**
 * Class encapsulating all options for command execution.
 * This reduces parameter passing and centralizes command options.
 */
public class CommandOptions {
    private final String fileName;
    private final String format;
    private final String category;
    
    private CommandOptions(Builder builder) {
        this.fileName = builder.fileName;
        this.format = builder.format;
        this.category = builder.category;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getFormat() {
        return format;
    }
    
    public String getCategory() {
        return category;
    }
    
    /**
     * Builder for CommandOptions.
     */
    public static class Builder {
        private String fileName;
        private String format = "json"; // default format
        private String category;
        
        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
        
        public Builder format(String format) {
            this.format = format;
            return this;
        }
        
        public Builder category(String category) {
            this.category = category;
            return this;
        }
        
        public CommandOptions build() {
            return new CommandOptions(this);
        }
    }
} 