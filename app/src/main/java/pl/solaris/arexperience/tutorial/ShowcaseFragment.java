package pl.solaris.arexperience.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.solaris.arexperience.R;
import pl.solaris.arexperience.metaio.RecognitionActivity;
import pl.solaris.arexperience.view.ShowcaseView;

/**
 * Created by pbednarz on 2015-02-16.
 */
public class ShowcaseFragment extends Fragment {

    private static final String ARG_SCANNER_TPE = "scanner_type";
    @InjectView(R.id.image)
    ImageView showcaseTarget;
    @InjectView(R.id.title)
    TextView showcaseTitle;
    @InjectView(R.id.description)
    TextView showcaseDetails;
    private ShowcaseView showcaseView;

    public static void startShowcase(final FragmentActivity activity,
                                     @RecognitionActivity.Scanner int scannerType) {
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction();
        ft.add(android.R.id.content, ShowcaseFragment.newInstance(scannerType));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack("showcase");
        ft.commit();
    }

    public static ShowcaseFragment newInstance(@RecognitionActivity.Scanner int scannerType) {
        ShowcaseFragment fragment = new ShowcaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCANNER_TPE, scannerType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        showcaseView = (ShowcaseView) inflater.inflate(R.layout.fragment_showcase, container, false);
        ButterKnife.inject(this, showcaseView);
        return showcaseView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switch (getArguments().getInt(ARG_SCANNER_TPE, RecognitionActivity.IMAGE_RECOGNITION)) {
            case RecognitionActivity.QR_CODE: {
                showcaseTitle.setText(getString(R.string.title_qr));
                showcaseDetails.setText(getString(R.string.desc_qr));
                showcaseTarget.setImageResource(R.drawable.ic_qr);
                break;
            }
            case RecognitionActivity.IMAGE_RECOGNITION:
            default: {
                showcaseTitle.setText(getString(R.string.title_recognition));
                showcaseDetails.setText(getString(R.string.desc_recognition));
                showcaseTarget.setImageResource(R.drawable.ic_eye);
            }

            showcaseDetails.setMovementMethod(new ScrollingMovementMethod());
        }

        showcaseTarget.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                showcaseTarget.getViewTreeObserver().removeOnPreDrawListener(this);
                showcaseView.setDrawCircle(showcaseTarget);
                return false;
            }
        });

    }

    @Override
    public void onDestroyView() {
        showcaseView = null;
        showcaseTitle = null;
        showcaseDetails = null;
        showcaseTarget = null;
        super.onDestroyView();
    }

    @OnClick(R.id.done_btn)
    public void doneClicked() {
        getActivity().onBackPressed();
    }
}