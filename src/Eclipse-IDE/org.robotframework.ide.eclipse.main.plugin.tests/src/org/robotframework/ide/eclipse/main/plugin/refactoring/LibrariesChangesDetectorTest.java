/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.refactoring;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.rf.ide.core.project.RobotProjectConfig;
import org.rf.ide.core.project.RobotProjectConfig.LibraryType;
import org.rf.ide.core.project.RobotProjectConfig.ReferencedLibrary;

public class LibrariesChangesDetectorTest {

    @Test
    public void virtualLibraryIsReportedForDeletion_whenPathIsEmptyAfterRefactoringAndIsChildOfPathBefore() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib1", "project/resource"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib2", "project/resource/lib.xml"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib3", "project/res/lib_a.xml"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib4", "project/res/lib_b.xml"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath, Optional.empty(),
                config);
        detector.detect(processor);

        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(0)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(1)));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void virtualLibraryIsReportedForModification_whenThereIsAPathAfterRefactoringAndIsChildOfPathBefore() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib1", "project/resource"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib2", "project/resource/lib.xml"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib3", "project/res/lib_a.xml"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.VIRTUAL, "lib4", "project/res/lib_b.xml"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath,
                Optional.of(Path.fromPortableString("project/different_resource")), config);
        detector.detect(processor);

        verify(processor).pathModified(same(config.getReferencedLibraries().get(0)), argThat(hasSameFields(
                ReferencedLibrary.create(LibraryType.VIRTUAL, "different_resource", "project/different_resource"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(1)), argThat(hasSameFields(
                ReferencedLibrary.create(LibraryType.VIRTUAL, "lib.xml", "project/different_resource/lib.xml"))));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void javaLibraryIsReportedForDeletion_whenPathIsEmptyAfterRefactoringAndIsChildOfPathBefore() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib1.c", "project/resource"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib2.c", "project/resource/lib.jar"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib3.c", "project/res/lib_a.jar"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib4.c", "project/res/lib_b.jar"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath, Optional.empty(),
                config);
        detector.detect(processor);

        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(0)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(1)));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void javaLibraryIsReportedForModification_whenThereIsAPathAfterRefactoringAndIsChildOfPathBefore() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib1.c", "project/resource"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib2.c", "project/resource/lib.jar"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib3.c", "project/res/lib_a.jar"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.JAVA, "lib4.c", "project/res/lib_b.jar"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath,
                Optional.of(Path.fromPortableString("project/different_resource")), config);
        detector.detect(processor);

        verify(processor).pathModified(same(config.getReferencedLibraries().get(0)), argThat(
                hasSameFields(ReferencedLibrary.create(LibraryType.JAVA, "lib1.c", "project/different_resource"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(1)), argThat(hasSameFields(
                ReferencedLibrary.create(LibraryType.JAVA, "lib2.c", "project/different_resource/lib.jar"))));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void pythonLibraryIsReportedForDeletion_whenPathIsEmptyAfterRefactoringAndIsChildOfPathBefore() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource/abc");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "d", "project/resource/abc/d.py"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "d.x", "project/resource/abc/d.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "abc.c", "project/resource/abc/c/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "abc.c.d", "project/resource/abc/c/d.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.x", "project/resource/x.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.abc", "project/resource/abc/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.abc.z", "project/resource/abc/z.py"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath, Optional.empty(),
                config);
        detector.detect(processor);

        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(0)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(1)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(2)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(3)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(5)));
        verify(processor).pathRemoved(same(config), same(config.getReferencedLibraries().get(6)));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void pythonLibraryIsReportedForModification_whenPathIsEmptyAfterRefactoringAndIsChildOfPathBefore() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource/abc");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "d", "project/resource/abc/d.py"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "d.x", "project/resource/abc/d.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "abc.c", "project/resource/abc/c/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "abc.c.d", "project/resource/abc/c/d.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.x", "project/resource/x.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.abc", "project/resource/abc/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.abc.z", "project/resource/abc/z.py"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath,
                Optional.of(Path.fromPortableString("project/resource/xyz")), config);
        detector.detect(processor);

        verify(processor).pathModified(same(config.getReferencedLibraries().get(0)),
                argThat(hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "d", "project/resource/xyz/d.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(1)), argThat(
                hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "d.x", "project/resource/xyz/d.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(2)), argThat(hasSameFields(
                ReferencedLibrary.create(LibraryType.PYTHON, "xyz.c", "project/resource/xyz/c/__init__.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(3)), argThat(
                hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "xyz.c.d", "project/resource/xyz/c/d.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(5)), argThat(hasSameFields(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.xyz", "project/resource/xyz/__init__.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(6)), argThat(hasSameFields(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.xyz.z", "project/resource/xyz/z.py"))));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void pythonLibraryIsReportedForModification_whenLibraryFileNameIsChanged() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource/abc/d.py");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "d", "project/resource/abc/d.py"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "d.x", "project/resource/abc/d.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "d", "project/resource/abc/d/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "abc.c", "project/resource/abc/c/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "abc.c.d", "project/resource/abc/c/d.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.x", "project/resource/x.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.abc", "project/resource/abc/__init__.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.abc.z", "project/resource/abc/z.py"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath,
                Optional.of(Path.fromPortableString("project/resource/abc/w.py")), config);
        detector.detect(processor);

        verify(processor).pathModified(same(config.getReferencedLibraries().get(0)),
                argThat(hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "w", "project/resource/abc/w.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(1)), argThat(
                hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "w.x", "project/resource/abc/w.py"))));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void pythonLibraryIsReportedForModification_whenLibraryFileIsMovedUp() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/resource/lib.py");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "lib.class", "project/resource/lib.py"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "lib", "project/resource/lib.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "lib.otherClass", "project/other/lib.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "resource.lib.otherClass", "project/resource/lib.py"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath,
                Optional.of(Path.fromPortableString("project/lib.py")), config);
        detector.detect(processor);

        verify(processor).pathModified(same(config.getReferencedLibraries().get(0)),
                argThat(hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "lib.class", "project/lib.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(1)),
                argThat(hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "lib", "project/lib.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(3)), argThat(
                hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "lib.otherClass", "project/lib.py"))));
        verifyNoMoreInteractions(processor);
    }

    @Test
    public void pythonLibraryIsReportedForModification_whenLibraryFileIsMovedDown() {
        @SuppressWarnings("unchecked")
        final RedXmlChangesProcessor<ReferencedLibrary> processor = mock(RedXmlChangesProcessor.class);

        final IPath beforeRefactorPath = Path.fromPortableString("project/lib.py");

        final RobotProjectConfig config = new RobotProjectConfig();
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "lib.class", "project/lib.py"));
        config.addReferencedLibrary(ReferencedLibrary.create(LibraryType.PYTHON, "lib", "project/lib.py"));
        config.addReferencedLibrary(
                ReferencedLibrary.create(LibraryType.PYTHON, "lib.otherClass", "project/other/lib.py"));

        final LibrariesChangesDetector detector = new LibrariesChangesDetector(beforeRefactorPath,
                Optional.of(Path.fromPortableString("project/resource/lib.py")), config);
        detector.detect(processor);

        verify(processor).pathModified(same(config.getReferencedLibraries().get(0)),
                argThat(hasSameFields(
                        ReferencedLibrary.create(LibraryType.PYTHON, "lib.class", "project/resource/lib.py"))));
        verify(processor).pathModified(same(config.getReferencedLibraries().get(1)),
                argThat(hasSameFields(ReferencedLibrary.create(LibraryType.PYTHON, "lib", "project/resource/lib.py"))));
        verifyNoMoreInteractions(processor);
    }


    private static ArgumentMatcher<ReferencedLibrary> hasSameFields(final ReferencedLibrary library) {
        return toMatch -> {
            return Objects.equals(library.getType(), toMatch.getType())
                    && Objects.equals(library.getName(), toMatch.getName())
                    && Objects.equals(library.getPath(), toMatch.getPath());
        };
    }
}
