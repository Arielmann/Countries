package com.example.matrixassignment.countriesdatascreen.model

/**
 * A helper class for sorting countries list by different parameters
 */
class CountriesSorter {

    var sortStrategy: CountrySortStrategy = CountrySortStrategy.NATIVE_NAME
    var orderStrategy: OrderStrategy = OrderStrategy.ASCENDING

    fun getComparator(): java.util.Comparator<in Country> {
        return orderStrategy.strategy.invoke(sortStrategy)
    }
}

/**
 * Determines the function for sorting a list of countries.
 * Although the task only asks for 2 parameters for sorting, this enum allows
 * to easily add more functions upon request
 */
enum class CountrySortStrategy(val strategy: (Country) -> Comparable<*>) {
    NATIVE_NAME({ it.name }), AREA({ it.area })
}

/**
 * Determines the function for ordering a list of countries.
 * Although the task only asks for 2 ways of ordering, this enum allows
 * to easily add more strategies upon request
 */
enum class OrderStrategy(internal val strategy: (CountrySortStrategy) -> Comparator<Country>) {
    ASCENDING({ compareBy(it.strategy) }), DESCENDING({ compareByDescending(it.strategy) });
}






