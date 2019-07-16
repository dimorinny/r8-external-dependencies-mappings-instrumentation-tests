package com.dimorinny.proguard

class KotlinClassWithMethodsInsideTestModule {
    fun method1(): List<String> = emptyList()
    fun method2(): Set<String> = setOf()
}