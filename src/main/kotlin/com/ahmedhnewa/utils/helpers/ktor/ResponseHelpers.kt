package com.ahmedhnewa.utils.helpers.ktor


//@Deprecated("Deprecated even before complete the implementation of the function")
// Please note that it's not related to ktor but mostly used in the response
/**
Convert K to T
 * For example convert ProductCategory to ProductCategoryResponse
 * ProductCategory is K
 * ProductCategoryResponse is T
 * */
@Deprecated("Just how stupid I'm, I forgot there is already a function called map in kotlin")
fun <T, K> List<K>.toListOf(eachOneToBeAdded: (K) -> T): List<T> {
    val returnList = mutableListOf<T>()
    this.forEach { i ->
        returnList.add(
            eachOneToBeAdded(i)
        )
    }
    return returnList
}