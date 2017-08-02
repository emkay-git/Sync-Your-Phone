package android.cloud.microsoft.com.serviceapplication;

import java.util.Observable;

/**
 * Created by mohit on 12/7/17.
 */
// taken from stackoverflow answer; on how to talk between activity and broadcast receiver - one solution observable pattern .
    // TODO: See if that can be done in any other simpler way.

    public class ObservableObject extends Observable {
        private static ObservableObject instance = new ObservableObject();

        public static ObservableObject getInstance() {
            return instance;
        }

        private ObservableObject() {
        }

        public void updateValue(Object data) {
            synchronized (this) {
                setChanged();
                notifyObservers(data);
            }
        }
    }

