package org.vaslabs.publisher

package object syntax {
  implicit final class ExtractorOps[A](val obj: A) extends AnyVal {
    def partitionKey(implicit partitionKeyExtractor: PartitionKeyExtractor[A]): String = partitionKeyExtractor.extractKey(obj)
  }
}
