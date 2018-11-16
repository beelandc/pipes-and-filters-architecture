package net.cbeeland.driver;

public class Main {

  public static void main(String[] args) {

    Cli cliOptions = new Cli(args);

    // Main Logic Flow defined in parse() method
    cliOptions.parse();

  }

}
