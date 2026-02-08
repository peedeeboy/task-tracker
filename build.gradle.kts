
tasks.register("runStack") {
    group = "application"
    dependsOn(":backend:bootRun", ":frontend:yarn_start")
}
