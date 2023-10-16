# SimpleKtorDemo

Trying to learn **[Ktor server](https://ktor.io/docs/intellij-idea.html)** by build some dummy project

If you want to run the project, you need to pass the following environment variables:

* EMAIL_PASSWORD
* EMAIL_USERNAME
* FROM_EMAIL
* JWT_SECRET
* DATABASE_URL

if you use IntelliJ IDEA, you can easily edit the configurations that launch the application and add them

I uploaded this project to help Ktor Community reproduce a specific bug, where the server will throw ClassCastException when kotlinx.serialization library is used with auto reload.
**[for more details](https://youtrack.jetbrains.com/issue/KTOR-5426/Autoreloading-ClassCastException-when-kotlinx.serialization-library-is-used)**
