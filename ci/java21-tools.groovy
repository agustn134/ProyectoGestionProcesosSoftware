import jenkins.model.Jenkins
import hudson.model.JDK
import hudson.tasks.Maven
import hudson.tools.InstallSourceProperty

def j = Jenkins.get()
def jdks = j.getJDKs().findAll { it.name != 'JDK21' }
jdks.add(new JDK('JDK21', '/opt/java/openjdk'))
j.setJDKs(jdks)
j.save()
def descriptor = j.getDescriptorByType(Maven.DescriptorImpl.class)
def installs = descriptor.getInstallations().findAll { it.name != 'Maven3' }
installs.add(new Maven.MavenInstallation('Maven3', '', [new InstallSourceProperty([new Maven.MavenInstaller('3.9.9')])]))
descriptor.setInstallations(installs as Maven.MavenInstallation[])
descriptor.save()
println('UNIDAD2: JDK21 y Maven3 3.9.9 configurados')
