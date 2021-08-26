package com.example.moneycalc

import androidx.room.*
import androidx.room.ForeignKey.SET_DEFAULT
import java.util.*

//region entities

@Entity
data class Account (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val accName: String?,
    val value: Long,
    val isAtSum: Boolean,
)

@Entity(foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category"], onDelete = SET_DEFAULT), ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["acc"], onDelete = SET_DEFAULT)])
data class Operation (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val acc: Long,
    val date: Calendar?,
    val sum: Long,
    val category: Long,
    val comment: String?,
    val isTech: Boolean
)

@Entity(foreignKeys = [ForeignKey(entity = MainCategory::class, parentColumns = ["id"], childColumns = ["mainCat"], onDelete = SET_DEFAULT)])
data class Category (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val catName: String?,
    val mainCat: Long,
    val pic: String,
)

@Entity
data class MainCategory (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val mainCatName: String?,
    val pic: String,
)

@Entity(foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["category"], onDelete = SET_DEFAULT), ForeignKey(entity = Account::class, parentColumns = ["id"], childColumns = ["acc"], onDelete = SET_DEFAULT)])
data class Debt (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val category: Long,
    val acc: Long,
    val dateFrom: Calendar,
    val dateTo: Calendar?,
    val sum: Long,
    val comment: String?,
)

//endregion entities

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: Account)

    @Update
    fun updateAccount(account: Account)

    @Delete
    fun deleteAccount(account: Account)

    @Query("SELECT * FROM Account WHERE accName == :accName")
    fun getAccountByName(accName: String): List<Account>

    @Query("SELECT * FROM Account")
    fun getAccounts(): List<Account>
}

@Dao
interface OperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperation(account: Operation)

    @Update
    fun updateOperation(account: Operation)

    @Delete
    fun deleteOperation(account: Operation)

    @Query("SELECT * FROM Operation WHERE accName == :accName")
    fun getOperationByName(accName: String): List<Operation>

    @Query("SELECT * FROM Operation")
    fun getOperations(): List<Operation>
}