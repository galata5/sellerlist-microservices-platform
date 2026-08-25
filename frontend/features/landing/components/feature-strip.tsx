import { Reveal } from "@/components/reveal";
import { Container } from "@/components/ui/container";

const features = [
  {
    title: "Clean product pages",
    description:
      "Browse the catalog, open product details, and add items to your cart without jumping through extra steps."
  },
  {
    title: "Straightforward checkout",
    description:
      "Customer details, order review, and confirmation now live in one simple flow that is easier to trust."
  },
  {
    title: "Order tracking",
    description:
      "Orders and payment progress stay visible in one place after purchase, without feeling like an admin tool."
  }
];

export function FeatureStrip() {
  return (
    <section className="pb-4">
      <Container className="grid gap-4 lg:grid-cols-3">
        {features.map((feature, index) => (
          <Reveal key={feature.title} delay={index * 0.08}>
            <article className="surface-panel h-full p-6">
              <span className="eyebrow">0{index + 1}</span>
              <h2 className="mt-4 text-xl font-semibold text-text">{feature.title}</h2>
              <p className="mt-3 text-sm leading-7 text-muted">{feature.description}</p>
            </article>
          </Reveal>
        ))}
      </Container>
    </section>
  );
}
