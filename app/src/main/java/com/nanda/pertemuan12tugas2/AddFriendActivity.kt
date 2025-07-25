package com.nanda.pertemuan12tugas2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.nanda.pertemuan12tugas2.database.AppDatabase
import com.nanda.pertemuan12tugas2.databinding.ActivityAddFriendBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class AddFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var database: AppDatabase
    private var friendId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)

        // Check if in edit mode
        intent.extras?.let { extras ->
            friendId = extras.getInt("FRIEND_ID", 0)
            binding.etName.setText(extras.getString("FRIEND_NAME"))
            binding.etPhone.setText(extras.getString("FRIEND_PHONE"))
            binding.etEmail.setText(extras.getString("FRIEND_EMAIL"))
            binding.etPhotoUrl.setText(extras.getString("FRIEND_PHOTO"))
            binding.btnSave.text = "Perbarui"
        }

        binding.btnSave.setOnClickListener {
            saveOrUpdateFriend()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun saveOrUpdateFriend() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val photoUrl = binding.etPhotoUrl.text.toString().trim()

        // Validate required fields
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Snackbar.make(binding.root, "Harap isi nama, telepon, dan email", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Convert and validate Google Drive URL or standard image URL
        val validatedPhotoUrl = if (photoUrl.isNotEmpty()) {
            when {
                isGoogleDriveUrl(photoUrl) -> {
                    val directUrl = convertGoogleDriveUrl(photoUrl)
                    if (directUrl != null) {
                        Log.d("AddFriendActivity", "Converted Google Drive URL: $directUrl")
                        directUrl
                    } else {
                        Log.w("AddFriendActivity", "Invalid Google Drive URL: $photoUrl")
                        Snackbar.make(binding.root, "URL Google Drive tidak valid. Gunakan tautan berbagi yang benar.", Snackbar.LENGTH_LONG).show()
                        null
                    }
                }
                isValidImageUrl(photoUrl) -> {
                    Log.d("AddFriendActivity", "Valid image URL: $photoUrl")
                    photoUrl
                }
                else -> {
                    Log.w("AddFriendActivity", "Invalid image URL: $photoUrl")
                    Snackbar.make(binding.root, "URL tidak valid. Gunakan tautan langsung ke gambar (jpg, png, dll.) atau Google Drive.", Snackbar.LENGTH_LONG).show()
                    null
                }
            }
        } else {
            null
        }

        val friend = Friend(
            id = friendId,
            name = name,
            phone = phone,
            email = email,
            photoUrl = validatedPhotoUrl
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (friendId == 0) {
                database.friendDao().insert(friend)
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.root, "Teman ditambahkan", Snackbar.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                database.friendDao().update(friend)
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.root, "Teman diperbarui", Snackbar.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun isGoogleDriveUrl(url: String): Boolean {
        val googleDrivePattern = Pattern.compile(
            "^https://drive.google.com/file/d/([a-zA-Z0-9_-]+)/?.*$",
            Pattern.CASE_INSENSITIVE
        )
        return googleDrivePattern.matcher(url).matches()
    }

    private fun convertGoogleDriveUrl(url: String): String? {
        val googleDrivePattern = Pattern.compile(
            "^https://drive.google.com/file/d/([a-zA-Z0-9_-]+)/?.*$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = googleDrivePattern.matcher(url)
        return if (matcher.find()) {
            val fileId = matcher.group(1)
            "https://drive.google.com/uc?export=download&id=$fileId"
        } else {
            null
        }
    }

    private fun isValidImageUrl(url: String): Boolean {
        val imageUrlPattern = Pattern.compile(
            "^https?://.*\\.(jpg|jpeg|png|gif|bmp|webp)(\\?.*)?$",
            Pattern.CASE_INSENSITIVE
        )
        return imageUrlPattern.matcher(url).matches()
    }
}