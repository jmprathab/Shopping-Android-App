import android.app.Application;
import timber.log.Timber;

/**
 * Created by jmprathab on 24/09/17.
 */

public class ShoppingApplication extends Application {
  @Override public void onCreate() {
    super.onCreate();
    //Planting Timber
    Timber.plant(new Timber.DebugTree());
  }
}
