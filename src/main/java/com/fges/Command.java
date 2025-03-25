package com.fges;

import java.util.List;

public interface Command {
    void execute(List<String> args, GroceryManager groceryManager) throws Exception;
}
