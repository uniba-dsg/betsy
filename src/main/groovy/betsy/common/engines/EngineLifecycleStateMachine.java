package betsy.common.engines;

import java.util.Objects;

public final class EngineLifecycleStateMachine implements EngineLifecycle {

    private final EngineLifecycle delegate;

    public EngineLifecycleStateMachine(EngineLifecycle delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public void install() {
        if(isInstalled()) {
            throw new IllegalStateException("Cannot install because engine is already installed");
        }
        if(isRunning()) {
            throw new IllegalStateException("Cannot install because engine is currently running");
        }
        delegate.install();
    }

    @Override
    public void uninstall() {
        if(!isInstalled()) {
            throw new IllegalStateException("Cannot uninstall because engine is not installed");
        }
        if(isRunning()) {
            throw new IllegalStateException("Cannot uninstall because engine is currently running");
        }
        delegate.uninstall();
    }

    @Override
    public boolean isInstalled() {
        return delegate.isInstalled();
    }

    @Override
    public void startup() {
        if(!isInstalled()) {
            throw new IllegalStateException("Cannot start because engine is not installed");
        }
        if(isRunning()) {
            throw new IllegalStateException("Cannot start because engine is currently running");
        }
        delegate.startup();
    }

    @Override
    public void shutdown() {
        if(!isInstalled()) {
            throw new IllegalStateException("Cannot shutdown because engine is not installed");
        }
        if(isRunning()) {
            throw new IllegalStateException("Cannot shutdown because engine is not running");
        }
        delegate.shutdown();
    }

    @Override
    public boolean isRunning() {
        return delegate.isRunning();
    }

}
