package com.example.mybooks;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddBookActivity extends AppCompatActivity {
    private TextInputEditText titleEditText;
    private TextInputEditText authorEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText imageUrlEditText;
    private TextInputEditText pdfUrlEditText;
    private MaterialButton saveButton;
    private MaterialButton deleteButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        titleEditText = findViewById(R.id.titleEditText);
        authorEditText = findViewById(R.id.authorEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        imageUrlEditText = findViewById(R.id.imageUrlEditText);
        pdfUrlEditText = findViewById(R.id.pdfUrlEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Check if we're editing an existing book
        bookId = getIntent().getStringExtra("bookId");
        if (bookId != null) {
            loadBook(bookId);
            deleteButton.setVisibility(View.VISIBLE);
        }

        // Setup save button
        saveButton.setOnClickListener(v -> saveBook());

        // Setup delete button
        deleteButton.setOnClickListener(v -> deleteBook());
    }

    private void loadBook(String bookId) {
        db.collection("books").document(bookId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Book book = documentSnapshot.toObject(Book.class);
                        if (book != null) {
                            titleEditText.setText(book.getTitle());
                            authorEditText.setText(book.getAuthor());
                            descriptionEditText.setText(book.getDescription());
                            imageUrlEditText.setText(book.getImageUrl());
                            pdfUrlEditText.setText(book.getPdfUrl());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading book", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void saveBook() {
        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String imageUrl = imageUrlEditText.getText().toString().trim();
        String pdfUrl = pdfUrlEditText.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> book = new HashMap<>();
        book.put("title", title);
        book.put("author", author);
        book.put("description", description);
        book.put("imageUrl", imageUrl);
        book.put("pdfUrl", pdfUrl);
        book.put("userId", userId);
        book.put("lastUpdated", new Date());

        if (bookId == null) {
            // Creating a new book
            book.put("createdAt", new Date());
            db.collection("books")
                    .add(book)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding book", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Updating existing book
            db.collection("books").document(bookId)
                    .update(book)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating book", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void deleteBook() {
        if (bookId != null) {
            db.collection("books").document(bookId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting book", Toast.LENGTH_SHORT).show();
                    });
        }
    }
} 