package com.example.capturaprospectos.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProspectoDao {

    @Query("SELECT * FROM prospectos ORDER BY id DESC")
    fun observarTodos(): Flow<List<ProspectoEntity>>

    @Query("SELECT * FROM prospectos ORDER BY id DESC")
    suspend fun obtenerTodos(): List<ProspectoEntity>

    @Query("SELECT * FROM prospectos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Long): ProspectoEntity?

    @Query("SELECT * FROM prospectos WHERE rut = :rut LIMIT 1")
    suspend fun obtenerPorRut(rut: String): ProspectoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(prospecto: ProspectoEntity): Long

    @Update
    suspend fun actualizar(prospecto: ProspectoEntity): Int

    @Query("UPDATE prospectos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Long, estado: String): Int

    @Delete
    suspend fun eliminar(prospecto: ProspectoEntity): Int

    @Query("DELETE FROM prospectos WHERE id = :id")
    suspend fun eliminarPorId(id: Long): Int
}