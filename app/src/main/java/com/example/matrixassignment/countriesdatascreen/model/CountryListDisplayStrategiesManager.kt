package com.example.matrixassignment.countriesdatascreen.model

/**
 * A helper class for maintaining data about the way a countries list should be displayed
 */
class CountryListDisplayStrategiesManager {

    var sortStrategy: CountrySortStrategy = CountrySortStrategy.NAME
    var orderStrategy: CountryOrderStrategy = CountryOrderStrategy.ASCENDING

    fun generateListForDisplay(countries: Collection<Country> ): List<Country>{
        return countries.sortedWith(orderStrategy.strategy.invoke(sortStrategy))
    }
}

/**
 * Anonymous functions providing ways to sort a list of countries.
 * Although the demo specifications only ask for 2 parameters for sorting with, this enum allows
 * to easily add more functions upon request
 */
enum class CountrySortStrategy(val strategy: (Country) -> Comparable<*>) {
    NAME({ it.name }), AREA({ it.area })
}

/**
 * Anonymous functions for comparing objects within a list of countries.
 * Although the demo specifications only ask for 2 ways of ordering, this enum allows
 * to easily add more strategies upon request
 */
enum class CountryOrderStrategy(internal val strategy: (CountrySortStrategy) -> Comparator<Country>) {
    ASCENDING({ compareBy(it.strategy) }), DESCENDING({ compareByDescending(it.strategy) });
}






