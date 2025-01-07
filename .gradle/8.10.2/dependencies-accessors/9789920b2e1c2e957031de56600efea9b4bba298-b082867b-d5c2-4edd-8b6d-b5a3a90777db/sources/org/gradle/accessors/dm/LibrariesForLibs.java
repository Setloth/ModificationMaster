package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final IoLibraryAccessors laccForIoLibraryAccessors = new IoLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>io</b>
     */
    public IoLibraryAccessors getIo() {
        return laccForIoLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class IoLibraryAccessors extends SubDependencyFactory {
        private final IoNettyLibraryAccessors laccForIoNettyLibraryAccessors = new IoNettyLibraryAccessors(owner);
        private final IoPapermcLibraryAccessors laccForIoPapermcLibraryAccessors = new IoPapermcLibraryAccessors(owner);

        public IoLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.netty</b>
         */
        public IoNettyLibraryAccessors getNetty() {
            return laccForIoNettyLibraryAccessors;
        }

        /**
         * Group of libraries at <b>io.papermc</b>
         */
        public IoPapermcLibraryAccessors getPapermc() {
            return laccForIoPapermcLibraryAccessors;
        }

    }

    public static class IoNettyLibraryAccessors extends SubDependencyFactory {
        private final IoNettyNettyLibraryAccessors laccForIoNettyNettyLibraryAccessors = new IoNettyNettyLibraryAccessors(owner);

        public IoNettyLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.netty.netty</b>
         */
        public IoNettyNettyLibraryAccessors getNetty() {
            return laccForIoNettyNettyLibraryAccessors;
        }

    }

    public static class IoNettyNettyLibraryAccessors extends SubDependencyFactory {

        public IoNettyNettyLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>all</b> with <b>io.netty:netty-all</b> coordinates and
         * with version reference <b>io.netty.netty.all</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getAll() {
            return create("io.netty.netty.all");
        }

    }

    public static class IoPapermcLibraryAccessors extends SubDependencyFactory {
        private final IoPapermcPaperLibraryAccessors laccForIoPapermcPaperLibraryAccessors = new IoPapermcPaperLibraryAccessors(owner);

        public IoPapermcLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.papermc.paper</b>
         */
        public IoPapermcPaperLibraryAccessors getPaper() {
            return laccForIoPapermcPaperLibraryAccessors;
        }

    }

    public static class IoPapermcPaperLibraryAccessors extends SubDependencyFactory {
        private final IoPapermcPaperPaperLibraryAccessors laccForIoPapermcPaperPaperLibraryAccessors = new IoPapermcPaperPaperLibraryAccessors(owner);

        public IoPapermcPaperLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>io.papermc.paper.paper</b>
         */
        public IoPapermcPaperPaperLibraryAccessors getPaper() {
            return laccForIoPapermcPaperPaperLibraryAccessors;
        }

    }

    public static class IoPapermcPaperPaperLibraryAccessors extends SubDependencyFactory {

        public IoPapermcPaperPaperLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>api</b> with <b>io.papermc.paper:paper-api</b> coordinates and
         * with version reference <b>io.papermc.paper.paper.api</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getApi() {
            return create("io.papermc.paper.paper.api");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final IoVersionAccessors vaccForIoVersionAccessors = new IoVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io</b>
         */
        public IoVersionAccessors getIo() {
            return vaccForIoVersionAccessors;
        }

    }

    public static class IoVersionAccessors extends VersionFactory  {

        private final IoNettyVersionAccessors vaccForIoNettyVersionAccessors = new IoNettyVersionAccessors(providers, config);
        private final IoPapermcVersionAccessors vaccForIoPapermcVersionAccessors = new IoPapermcVersionAccessors(providers, config);
        public IoVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.netty</b>
         */
        public IoNettyVersionAccessors getNetty() {
            return vaccForIoNettyVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.io.papermc</b>
         */
        public IoPapermcVersionAccessors getPapermc() {
            return vaccForIoPapermcVersionAccessors;
        }

    }

    public static class IoNettyVersionAccessors extends VersionFactory  {

        private final IoNettyNettyVersionAccessors vaccForIoNettyNettyVersionAccessors = new IoNettyNettyVersionAccessors(providers, config);
        public IoNettyVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.netty.netty</b>
         */
        public IoNettyNettyVersionAccessors getNetty() {
            return vaccForIoNettyNettyVersionAccessors;
        }

    }

    public static class IoNettyNettyVersionAccessors extends VersionFactory  {

        public IoNettyNettyVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>io.netty.netty.all</b> with value <b>4.1.111.Final</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getAll() { return getVersion("io.netty.netty.all"); }

    }

    public static class IoPapermcVersionAccessors extends VersionFactory  {

        private final IoPapermcPaperVersionAccessors vaccForIoPapermcPaperVersionAccessors = new IoPapermcPaperVersionAccessors(providers, config);
        public IoPapermcVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.papermc.paper</b>
         */
        public IoPapermcPaperVersionAccessors getPaper() {
            return vaccForIoPapermcPaperVersionAccessors;
        }

    }

    public static class IoPapermcPaperVersionAccessors extends VersionFactory  {

        private final IoPapermcPaperPaperVersionAccessors vaccForIoPapermcPaperPaperVersionAccessors = new IoPapermcPaperPaperVersionAccessors(providers, config);
        public IoPapermcPaperVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.io.papermc.paper.paper</b>
         */
        public IoPapermcPaperPaperVersionAccessors getPaper() {
            return vaccForIoPapermcPaperPaperVersionAccessors;
        }

    }

    public static class IoPapermcPaperPaperVersionAccessors extends VersionFactory  {

        public IoPapermcPaperPaperVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>io.papermc.paper.paper.api</b> with value <b>1.21.1-R0.1-SNAPSHOT</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getApi() { return getVersion("io.papermc.paper.paper.api"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
