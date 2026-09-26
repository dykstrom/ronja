# Search

## Evaluation range

- `ALPHA_START` and `BETA_START` in `AlphaBetaFinder` MUST lie outside every value
  `Evaluator.evaluate` can return.
- A new or raised constant in `Evaluator` MUST stay below `BETA_START` in
  magnitude. The largest today is `ILLEGAL_CHECK_VALUE` at 2,000,000, against
  bounds of plus and minus 3,000,000.
- Source: the alpha-beta algorithm needs starting bounds that no score can reach,
  otherwise the first comparison at a node cannot improve on them. Confirmed by
  the maintainer. No ADR records this.
