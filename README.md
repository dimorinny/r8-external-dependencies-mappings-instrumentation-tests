## Run instrumentation tests with minified applications (https://issuetracker.google.com/issues/137671763)

We're going to run instrumentation tests along with production (minified) application.
We want to cover all cases (including incorrect proguard (r8) configurations) and test exactly
the same APK, that will be released to our users.

But now we have several issues mostly about inconsistent between methods from proguarded production APK
and method references from test APK. 

### Bug

Right now I want to consider mismatching between proguarded external library inside target application
(in that case kotlin standard library) and references to it from test application issue.

We have application, that has kotlin standard library as dependency. That allows us to use it from
test application as well because of shared classpath.

I got from that conversation (https://issuetracker.google.com/issues/136284002), that all renames from
target application also should be applied to test application by applying mappings feature. It works well,
when we use renamed classes exactly from target application inside test application. But it doesn't work for renamed
classes from external dependencies inside target application, that we're trying to use in our test application.

Test application still has references to original (not proguarded) methods. Looks like apply mappings feature
doesn't work well for external dependencies.

### How to reproduce?

1. Clone project:
```
git clone https://github.com/dimorinny/r8-external-dependencies-mappings-instrumentation-tests
```


2. Build test and target APKs
```
./gradlew app:assembleAndroidTest
```


3. Open target APK (`app/build/outputs/apk/release/app-release-unsigned.apk`), and you will able
to see, that kotlin standard library has been proguarded (renamed), because `KotlinClassWithMethods`
uses proguarded (renamed) version of methods `emptyList()` and `setOf()` and also all class references.
<div align="center">
    <img height="350px" src="https://raw.githubusercontent.com/dimorinny/r8-external-dependencies-mappings-instrumentation-tests/master/images/proguarded_kotlin_stdlib_in_target_apk.jpg">
</div>


4. Open test APK (`app/build/outputs/apk/androidTest/release/app-release-androidTest.apk`), and you 
will able to see that `KotlinClassWithMethodsInsideTestModule` uses full named (not proguarded) kotlin
standard library names:
<div align="center">
    <img height="350px" src="https://raw.githubusercontent.com/dimorinny/r8-external-dependencies-mappings-instrumentation-tests/master/images/kotlin_stdlib_in_test_application.jpg">
</div>
also, it has references to original names from kotlin standard library, that must be satisfied by target application:
<div align="center">
    <img src="https://raw.githubusercontent.com/dimorinny/r8-external-dependencies-mappings-instrumentation-tests/master/images/kotlin_stdlib_in_test_application_references.jpg">
</div>
but it is unpossible, because target application has proguarded kotlin standard library.


5. Sign APKs by`jarsigner`

6. Run instrumentation tests using command:
```
adb shell am instrument -w -r -e debug false -e class 'com.dimorinny.proguard.KotlinClassWithMethodsInsideTestModule#useAppContext' com.dimorinny.proguard.test/android.support.test.runner.AndroidJUnitRunner
```

As a result you will have a exception:

```
java.lang.NoClassDefFoundError: Failed resolution of: Lkotlin/collections/CollectionsKt;
	at com.dimorinny.proguard.KotlinClassWithMethodsInsideTestModule.method1(KotlinClassWithMethodsInsideTestModule.kt:4)
	at com.dimorinny.proguard.ExampleInstrumentedTest.useAppContext(ExampleInstrumentedTest.java:19)
	at java.lang.reflect.Method.invoke(Native Method)
	...
	at android.app.Instrumentation$InstrumentationThread.run(Instrumentation.java:1853)
```

### Definition of done

Applying mappings must be works for external dependencies.
Exactly in this case, in test application we should have proguarded (renamed) references to kotlin
standard library.