package katowulf.mucklegacy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Logger;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public void increment(View view) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Firebase", "calculate new score");
                if(dataSnapshot.exists()) {
                    ref.setValue(200);
                }
                else {
                    ref.setValue(100);
                }
            }
            @Override
            public void
            onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseError", firebaseError.toString());
            }
        });
    }

    public void transaction(View view) {
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                if( currentData.getValue() == null ) {
                    currentData.setValue(100);
                }
                else {
                    currentData.setValue((Long)currentData.getValue() + 100);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if( firebaseError != null ) {
                    Log.e("FirebaseError", firebaseError.toString());
                }
            }
        });
    }

    public void reset(View view) {
        ref.removeValue();
    }

    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
        ref = new Firebase("https://kato-sandbox.firebaseio.com/tmp/testscore");
        ref.keepSynced(true);

        setContentView(R.layout.activity_main);

        monitorScore((TextView)findViewById(R.id.textView), ref);
    }

    private static void monitorScore(final TextView view, final Firebase ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = String.valueOf((Long)dataSnapshot.getValue());
                if( value == null ) { value = "null"; }
                view.setText(value);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseError", firebaseError.toString());
            }
        });
    }
}
