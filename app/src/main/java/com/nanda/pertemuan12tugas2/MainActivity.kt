package com.nanda.pertemuan12tugas2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nanda.pertemuan12tugas2.adapter.FriendAdapter
import com.nanda.pertemuan12tugas2.database.AppDatabase
import com.nanda.pertemuan12tugas2.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        setupRecyclerView()
        loadFriends()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddFriendActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadFriends()
    }

    private fun setupRecyclerView() {
        friendAdapter = FriendAdapter(
            onEditClick = { friend ->
                val intent = Intent(this, AddFriendActivity::class.java).apply {
                    putExtra("FRIEND_ID", friend.id)
                    putExtra("FRIEND_NAME", friend.name)
                    putExtra("FRIEND_PHONE", friend.phone)
                    putExtra("FRIEND_EMAIL", friend.email)
                    putExtra("FRIEND_PHOTO", friend.photoUrl)
                }
                startActivity(intent)
            },
            onDeleteClick = { friend ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.friendDao().delete(friend) // Tidak perlu suspend
                    withContext(Dispatchers.Main) {
                        Snackbar.make(binding.root, "Friend deleted", Snackbar.LENGTH_SHORT).show()
                        loadFriends()
                    }
                }
            }
        )
        binding.rvFriends.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = friendAdapter
        }
    }

    private fun loadFriends() {
        CoroutineScope(Dispatchers.IO).launch {
            val friends = database.friendDao().getAllFriends() // Tidak perlu suspend
            withContext(Dispatchers.Main) {
                friendAdapter.submitList(friends)
            }
        }
    }
}