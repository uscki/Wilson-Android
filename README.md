This repository contains the code base for the Android version of the Wilson mobile application for U.S.C.K.I Incognito. This application is maintained by the AppCKI committee.

This application handles all the front-end logic. Communication with the database is handled through the B.A.D.W.O.L.F. API, for which documentation is available on the U.S.C.K.I Incognito website.

# Getting started
For contributing code, you need to be granted access to the repository by the AppCKI.

## Clone
To work on this code, clone the repository to your local machine, using Git:
```
git clone https://git.dev.uscki.nl/AppCKI/appcki-native-android.git -b development
cd appcki-native-android
```

Or using Android Studio directly*:
`File` --> `New` --> `Project from Version Control` --> `Git`
URL: https://git.dev.uscki.nl/AppCKI/appcki-native-android.git
Directory: Up to you.

Click `Test` and enter your U.S.C.K.I Incognito credentials.

* IF Android Studio complains about not knowing where to find Git, download Git from https://git-scm.com/downloads for your operating system.

## Configure & Build
After opening the project in Android Studio (either manually from the cloned Git repository, or directly through Git integration in Android Studio), the project needs to be configured.

Add two new files (easiest to use your explorer or the command line, as Android Studio hides your directory structure):
* `app/src/master/res/values/api.xml`
* `app/src/development/res/values/api.xml`

The contents of these files can be found on the [AppCKI WiCKI](https://www.uscki.nl/?pagina=Wicki&subject=APPCKI-WiCKI&forwarded=startAppCKI) (if you have been granted access to the WiCKI) and point Android to the location of the API.

If everything is right, you can now build the code through `Build` -> `Rebuild project`.

Either hook up your Android phone, or install an emulator to run the code.

## Contributing
If you are new to Android, read the [Getting Started Guide](https://www.uscki.nl/?pagina=Wicki&subject=AppCKI_Learning_Development)

This guide may also be useful as a starter on understanding the file structure used by the AppCKI, although depending on your level of experience, it may be a bit boring. If you feel like discovering this structure for yourselve, feel free.

When you want to have your code added to the application, push to a new branch. The branch name should be at least somewhat descriptive of your contribution, and follows the format `<prefix>/<description>` where `<prefix>` is `feature` (for new features), `fix` (for bugfixes), `refactor` (for heavy refactors of part of the code). 

Try to not do too much on a single branch. If you have unrelated features, which require significant coding, create them on seperate branches.

Go to this repository on git.dev.uscki.nl and create a pull request to the `development` branch for your contribution. Use a clear title, shortly explain your contribution, and, if necessary, write some added information about design choices you made. Your changes will be reviewed by a senior member of the AppCKI, who might ask additional questions about design decisions, or who might ask you to make changes. These changes can relate to style, naming of variables, comments to your code, or other things.

**Do not merge the merge request yourself**
Requests will be merged by a senior member of the AppCKI, after it has passed review. We want to keep development clean, which is why we have these checks.

**A comment on requested changes:**
The current code quality of the project is somewhat abismal. This might make some requested changes seem arbitrarily harhs, but please don't let this discourage you. We are trying to slowly move to better code, which is why we ask higher standards for newly contributed code than for existing code. Hope you understand.

## Style guide
Where possible, write code that is understandable. An incomplete list of pointers:
* Use clear variable, method, class and field names
* Declare variables close to where they are used
* Use (string) resources where possible; don't hard code
* Document the behavior of methods clearly
* Avoid spelling errors
* Make use of those indentations
* Don't commit everything in one go. Make changes within a commit relate to each other
* Use clear commit messages

Because we may work with less experienced developers in the future, a less obvious point is also:
* If concise code is hard to understand, sometimes less concise is better

