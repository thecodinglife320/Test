package com.ad.test

import android.content.Intent
import android.content.res.Resources
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import androidx.collection.ArrayMap
import androidx.collection.ArraySet
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.util.contains
import androidx.core.util.size
import com.ad.test.learn.Main3a
import com.ad.test.learn.Person

@Preview
@Composable
fun A(modifier: Modifier = Modifier) {
    val resources = LocalResources.current
    Text(
        resources.getScore(2, 10)
    )
}

fun Resources.getScore(pointsScored: Int, totalPoints: Int): String {
    return getString(
        R.string.students_score,
        getQuantityString(R.plurals.points, pointsScored, pointsScored),
        totalPoints
    )
}

@Preview
@Composable
fun B() {
    val cats = ArrayMap<String, Cat>().apply {
        put("Barsik", Cat())
        put("Murlok", Cat())
        put("Vasya", Cat())
    }
    Column {
        cats.forEach { (name, value) ->
            Text("$name: $value")
        }
        val words = ArraySet(listOf("The", "grass", "is", "green."))
        words.forEach {
            Text(it)
        }
        for (i in 0 until words.size) {
            Text(words.valueAt(i))
        }
    }
}

@Composable
fun C() {
    val catById = SparseArray<String>().apply {
        put(1, "Murlok")
        put(5, "Barsik")
        put(16, "Vasya")
    }
    val salaryByEmpId = SparseLongArray().apply {
        put(1, 100L)
        put(5, 99L)
        put(16, 100L)
        put(42, 9223372036854775807L)
        put(56, 130L)
    }

    val interestingEpisodes = SparseBooleanArray().apply {
        put(1, true)
        put(2, true)
        put(4, false)
    }

    val catOwners = SparseIntArray().apply {
        put(9, 1)
        put(34, 1)
        put(38, 42)
    }


    fun salaryOf(empId: Int): Long {
        val salary = salaryByEmpId.get(empId, -1L)
        if (salary < 0) throw NoSuchElementException("emp#$empId")
        return salary
    }

    fun seenEpisode(id: Int) =
        interestingEpisodes.contains(id)

    fun printCatOwners() {
        for (i in 0 until catById.size) {
            val catId = catById.keyAt(i)
            val catName = catById.valueAt(i)
            val ownerId = catOwners.get(catId, -1)
            print("Cat $catName ")
            println(if (ownerId < 0) "has no owner" else "is owned by #$ownerId")
        }
    }

    data class City(val name: String, val population: Int, val yearOfFoundation: Int)

    val cities = ArraySet<City>()
    cities.add(City("Toronto", 2731571, 1793))
    cities.add(City("Tokyo", 37274000, 1943))
    cities.add(City("Ufa", 1135061, 1574))
    println(cities.minByOrNull { it.yearOfFoundation }!!)
}

@Preview
@Composable
fun D() {
    val context = LocalContext.current
    val intent = Intent(context, Main3a::class.java)

    intent.putExtra("person", Person("0", "John", 22))
    intent.extras?.getParcelable<Person>("person")
}

class Cat