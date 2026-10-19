package com.example.capturaprospectos.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProspectoDao {
    @Insert
    suspend fun insertar(prospecto: ProspectoEntity): Long

    @Query("SELECT * FROM prospectos ORDER BY id DESC")
    suspend fun obtenerTodos(): List<ProspectoEntity>


    @Query("UPDATE prospectos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Long, estado: String)


    @Query("DELETE FROM prospectos WHERE id = :id")
    suspend fun eliminar(id: Long)
}
