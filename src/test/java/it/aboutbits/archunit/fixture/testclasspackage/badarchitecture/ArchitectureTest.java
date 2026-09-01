package it.aboutbits.archunit.fixture.testclasspackage.badarchitecture;

/// The same name, outside an architecture package. The exemption comes from the package, not from
/// the class being called ArchitectureTest, so this one is still reported.
class ArchitectureTest {
}
