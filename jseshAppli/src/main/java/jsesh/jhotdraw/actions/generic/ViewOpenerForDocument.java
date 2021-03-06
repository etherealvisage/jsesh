package jsesh.jhotdraw.actions.generic;

import org.jhotdraw_7_6.app.Application;
import org.jhotdraw_7_6.app.View;

import java.net.URI;

/**
 * Class in charge of opening a new view for a given document.
 * TODO: make a better OO system out of it, it's lousy.
 * TODO: 2019/03/22 : it's still lousy - and it was buggy too.
 */
public class ViewOpenerForDocument {

    private Application application;


    public ViewOpenerForDocument(Application application) {
        this.application = application;
    }

    public void openViewFromURI(final View view, final URI uri) {
        application.fixMultipleOpenId(view, uri);
        // Open the file
        view.execute(new ViewOpenerWorker(uri, view, application));
    }
}
