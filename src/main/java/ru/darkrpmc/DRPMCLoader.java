package ru.darkrpmc;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class DRPMCLoader implements PluginLoader {
    private static MavenLibraryResolver addDepends(final MavenLibraryResolver resolver, final String... names) {
        for (final String name : names)
            resolver.addDependency(new Dependency(new DefaultArtifact(name), null));
        return resolver;
    }

    @Override
    public void classloader(@NotNull final PluginClasspathBuilder classpathBuilder) {
        final MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder(
                "central", "default", "https://repo.maven.apache.org/maven2/"
        ).build());
        classpathBuilder.addLibrary(addDepends(resolver,
                "org.jetbrains.exposed:exposed-core:0.57.0",
                "org.jetbrains.exposed:exposed-jdbc:0.57.0",
                "org.jetbrains.exposed:exposed-java-time:0.57.0",
                "org.jetbrains.kotlin:kotlin-stdlib:2.1.0"
        ));
    }
}