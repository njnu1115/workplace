class Wayvnc < Formula
  desc "Description of wayvnc"
  homepage "https://example.com/your_project"
  url "https://github.com/any1/wayvnc/archive/refs/tags/v0.8.0.tar.gz"
  sha256 "075dcbe321d51ee5e6b59467f2d2fa313d49254fe574f9d6caf400f3a2ffd368"

  depends_on "meson" => :build
  depends_on "ninja" => :build
  depends_on "pkg-config" => :build
  depends_on "wayland" => :build
  depends_on "neatvnc" => :build
  depends_on "pixman" => :build
  depends_on "libdrm" => :build
  depends_on "aml" => :build

  def install
    system "meson", *std_meson_args, ".", "output"
    system "ninja", "-C", "output"
    system "ninja", "-C", "output", "install"
  end
end
