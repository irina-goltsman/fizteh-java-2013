package ru.fizteh.fivt.students.kochetovnicolai.shell;

import java.io.*;
import java.util.HashMap;

class Launcher {

    private HashMap<String, Executable> commands;

    Launcher(HashMap<String, Executable> commandsMap) {
        commands = commandsMap;
    }

    public boolean launch(String args[], Manager manager) throws IOException {
        if (args.length == 0) {
            return exec(System.in, manager, false);
        } else {

            StringBuilder builder = new StringBuilder();

            for (String arg : args) {
                builder.append(arg);
                builder.append(" ");
            }
            String string = builder.toString().replaceAll(";", "\n");

            byte[] bytes = string.getBytes("UTF-8");
            InputStream inputStream = new ByteArrayInputStream(bytes);

            return exec(inputStream, manager, true);
        }
    }

    private boolean exec(InputStream input, Manager manager, boolean isPackage) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        while (true) {
            if (!isPackage) {
                manager.printSuggestMessage();
            }
            String commandName = reader.readLine();
            if (commandName == null) {
                break;
            }
            String tokens[] = commandName.trim().split("[\\s]");

            if (tokens.length == 0 || tokens[0].equals("")) {
                continue;
            }

            boolean success = false;

            if (!commands.containsKey(tokens[0])) {
                manager.printMessage(tokens[0] + ": command not found");
            } else {
                Executable command = commands.get(tokens[0]);
                if (command.argumentsNumber() != tokens.length) {
                    manager.printMessage(tokens[0] + ": invalid number of arguments");
                } else {
                    success = command.execute(tokens);
                }
            }

            if (!success && isPackage) {
                return false;
            }

            if (manager.timeToExit()) {
                return true;
            }
        }
        return true;
    }
}
