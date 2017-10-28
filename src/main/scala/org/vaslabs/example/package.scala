package org.vaslabs.example

package object example {
  implicit final class ExtractorOps[A](val obj: A) extends AnyVal {
    def partitionKey(implicit partitionKeyExtractor: PartitionKeyExtractor[A]): String = partitionKeyExtractor.extractKey(obj)
  }
}
