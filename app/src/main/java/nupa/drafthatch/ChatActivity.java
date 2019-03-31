package nupa.drafthatch;

import android.icu.text.DateFormat;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        final ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.individual_chat, FirebaseDatabase.getInstance().getReference().child("Chats")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(android.text.format.DateFormat.format("dd-MM (HH:mm:ss)",model.getMessageTime()));
                adapter.notifyDataSetChanged();
            }
        };


        listOfMessages.setAdapter(adapter);
        listOfMessages.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        listOfMessages.setStackFromBottom(true);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                FirebaseDatabase.getInstance().getReference().child("Chats").push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                input.setText("");
            }
        });

    }
}
