package ru.darkrpmc.data.config

class TreeNode(private val name: String, vararg children: TreeNode): Collection<TreeNode> {
    private val children = ArrayList(children.asList())

    override fun isEmpty() = children.isEmpty()
    override fun iterator() = children.iterator()
    override val size get() = children.size
    override fun containsAll(elements: Collection<TreeNode>) = elements.all(this::contains)
    override fun contains(element: TreeNode) = element in children
    operator fun plusAssign(child: TreeNode) { children.add(child) }
    operator fun plusAssign(child: String) { children.add(TreeNode(child)) }
    override fun toString() = buildString { print(this, "", "") }

    private fun print(buffer: StringBuilder, prefix: String, childrenPrefix: String) {
        buffer.append(prefix).append(name).append('\n')
        children.forEachIndexed { index, child ->
            val isLast = index == children.size - 1
            val p = childrenPrefix + buildString { repeat(name.length / 2 - 1 + name.length % 2) { append(' ') } }
            child.print(buffer,
                if (isLast) "$p└─ " else "$p├─ ",
                if (isLast) "$p   " else "$p│  "
            )
        }
    }
}