import org.chromattic.api.annotations.Name
import org.chromattic.api.annotations.Property
import org.chromattic.api.annotations.PrimaryType

@PrimaryType(name = "gs:page") class Page {
	@Name def String name
	@Property(name = "title") def String title
	@Property(name = "content") def String content
}
