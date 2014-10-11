package me.tatarka.rxloader;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import rx.Observer;

/**
 * Persists the task by running it in a fragment with {@code setRetainInstanceState(true)}. This is
 * used internally by {@link RxLoaderManager}.
 *
 * @author Evan Tatarka
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RxLoaderBackendNestedFragment extends Fragment implements RxLoaderBackend {
    private WeakReference<RxLoaderBackendFragmentHelper> helperRef;
    
    private RxLoaderBackendFragmentHelper getHelper() {
        if (helperRef != null) {
            return helperRef.get();
        } else {
            Activity activity = getActivity();
            if (activity == null) {
                return null;
            }

            RxLoaderBackendFragment backendFragment = (RxLoaderBackendFragment) activity
                    .getFragmentManager().findFragmentByTag(RxLoaderManager.FRAGMENT_TAG);
            if (backendFragment == null) {
                backendFragment = new RxLoaderBackendFragment();
                activity.getFragmentManager().beginTransaction()
                        .add(backendFragment, RxLoaderManager.FRAGMENT_TAG)
                        .commit();
            }

            RxLoaderBackendFragmentHelper helper = backendFragment.getHelper();
            helperRef = new WeakReference<RxLoaderBackendFragmentHelper>(helper);
            return helper;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.onCreate(getId(), savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity().isFinishing()) {
            RxLoaderBackendFragmentHelper helper = getHelper();
            if (helper != null) {
                helper.onDestroy(getId());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.onSaveInstanceState(outState);
        }
    }

    @Override
    public <T> CachingWeakRefSubscriber<T> get(String tag) {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            return helper.get(getId(), tag);
        }
        return null;
    }

    @Override
    public <T> void put(String tag, CachingWeakRefSubscriber<T> subscriber) {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.put(getId(), tag, subscriber);
        }
    }

    @Override
    public <T> void setSave(String tag, Observer<T> observer, WeakReference<SaveCallback<T>> saveCallbackRef) {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.setSave(getId(), tag, observer, saveCallbackRef);
        }
    }

    @Override
    public void unsubscribeAll() {
        RxLoaderBackendFragmentHelper helper = getHelper();
        if (helper != null) {
            helper.unsubscribeAll(getId());
        }
    }
}