package com.fges;

import java.util.List;

public class ListCommand implements Command {

    @Override
    public void execute(List<String> args, GroceryManager groceryManager) {
        groceryManager.getGroceryList().forEach(System.out::println);
    }
}
