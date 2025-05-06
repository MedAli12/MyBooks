package com.example.mybooks;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    private List<Book> filteredList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(List<Book> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.filteredList = new ArrayList<>(bookList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = filteredList.get(position);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        holder.descriptionTextView.setText(book.getDescription());

        // Load book cover image
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(book.getImageUrl())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .into(holder.bookImageView);
        } else {
            holder.bookImageView.setImageResource(R.drawable.ic_book_placeholder);
        }

        // Setup PDF button
        if (book.getPdfUrl() != null && !book.getPdfUrl().isEmpty()) {
            holder.viewPdfButton.setVisibility(View.VISIBLE);
            holder.viewPdfButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(book.getPdfUrl()));
                holder.itemView.getContext().startActivity(intent);
            });
        } else {
            holder.viewPdfButton.setVisibility(View.GONE);
        }

        // Setup edit button
        holder.editButton.setOnClickListener(v -> listener.onBookClick(book));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filter(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(bookList);
        } else {
            text = text.toLowerCase();
            for (Book book : bookList) {
                if (book.getTitle().toLowerCase().contains(text) ||
                    book.getAuthor().toLowerCase().contains(text) ||
                    (book.getDescription() != null && book.getDescription().toLowerCase().contains(text))) {
                    filteredList.add(book);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView descriptionTextView;
        MaterialButton viewPdfButton;
        MaterialButton editButton;

        BookViewHolder(View itemView) {
            super(itemView);
            bookImageView = itemView.findViewById(R.id.bookImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            viewPdfButton = itemView.findViewById(R.id.viewPdfButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
} 