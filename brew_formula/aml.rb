class Aml < Formula
  desc "aml"
  homepage "https://gstreamer.freedesktop.org/"
  url "https://github.com/any1/aml/archive/refs/tags/v0.3.0.tar.gz"
  sha256 "cba1ca1689d4031faf37bb7a184559106b6d2f462ae8890a9fa16e3022ca1eb0"
  license "LGPL-2.1-or-later"

  depends_on "meson" => :build
  depends_on "ninja" => :build

  def install
    system "meson", *std_meson_args, ".", "output"
    system "ninja", "-C", "output"
    system "ninja", "-C", "output", "install"
  end

end
