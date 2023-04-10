# TwentyTen Launcher

![Java](https://img.shields.io/badge/Java-8%2B-blue?style=for-the-badge)
![GitHub Downloads](https://img.shields.io/github/downloads/Kawaxte/TwentyTenLauncher/total?style=for-the-badge)

![GitHub Latest](https://img.shields.io/github/v/release/Kawaxte/TwentyTenLauncher?sort=date&label=latest&color=green&style=for-the-badge)
![GitHub Pre-Release](https://img.shields.io/github/v/release/Kawaxte/TwentyTenLauncher?include_prereleases&sort=date&label=pre-release&color=orange&style=for-the-badge)

This project lets you play older versions of Minecraft that were released between June 2010 and
January 2011. It's named after the year the original Minecraft launcher was released, and it's
designed to give you an authentic experience of playing Minecraft as it was in the past.

## Features

- Play different versions of Minecraft from the past, including versions from June 2010 up to
  January 2011.

- Hear the classic sounds of Minecraft, like the scary cave sounds and the infamous hurt sound.
- Join servers that still run older versions of Minecraft, and see your skin or cape (_if the
  version
  supports it_) in both singleplayer and multiplayer.
- Use legacy mods just like you would in the past - just drag and drop `.class` files into the
  client `.jar` file, provided that you have ModLoader installed first.
- Change the language of the launcher without having to restart the application.

---

## Contributing

If you find this project useful or interesting, please consider giving it a ⭐. It's a
quick and easy way to show your support.

### Reporting Issues or Suggesting Features

1. Go to the [issue tracker](https://github.com/Kawaxte/TwentyTenLauncher/issues) on this
   repository.
2. Click the `New issue` button.
3. Describe the issue or suggestion clearly and provide any relevant details.
4. Click the `Submit new issue` button to create the issue.

### Forking and Making Pull Requests

If you are a developer and would like to contribute by adding a new feature or
fixing a bug, we encourage you to fork our repository on GitHub and make a pull request with your
changes. Here's how to get started:

1. Make a copy of this repository by forking it on GitHub.
2. Make the changes you want in a new branch of your forked repository.
3. Test your changes thoroughly.
4. Submit a pull request from your forked repository with a detailed
   explanation of your changes.

---

## Building from Source

This project uses [Maven](https://maven.apache.org/) to manage dependencies and build the
application. To build the project, you will need to have Maven installed on your computer.

1. Clone the repository to your computer by using [Git](https://git-scm.com/) or by downloading the
   source code as a `.zip` file.
2. Load the project into your IDE of choice or open a terminal in the project directory.
3. Make any changes you want to the source code.
4. Run the command `mvn clean package shade:shade` to build the project or use the Run Configuration
   in your IDE. The finished build will be saved as a `-shaded.jar` file in the `target` directory.