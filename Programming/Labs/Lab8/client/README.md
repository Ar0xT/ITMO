# Lab8 Client (JavaFX)

This module provides the JavaFX client UI. The login screen is wired to the existing backend auth flow by sending the `__get_commands__` request with provided credentials.

## Run

Build the client module:

```powershell
mvn -pl client -am test
```

To launch the UI, run the `client.app.JavaFxLauncher` main class from your IDE or provide a JavaFX runtime on the command line:

```powershell
java -cp client/target/classes;common/target/classes client.app.JavaFxLauncher localhost 5000
```

If you use a JavaFX SDK outside the JDK, add the JavaFX modules to the `--module-path` and `--add-modules` options.

